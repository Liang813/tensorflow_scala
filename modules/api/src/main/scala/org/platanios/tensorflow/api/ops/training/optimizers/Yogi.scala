/* Copyright 2017-19, Emmanouil Antonios Platanios. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.platanios.tensorflow.api.ops.training.optimizers

import org.platanios.tensorflow.api.core.Shape
import org.platanios.tensorflow.api.core.types.{IsIntOrLong, IsNotQuantized, TF}
import org.platanios.tensorflow.api.implicits.Implicits._
import org.platanios.tensorflow.api.ops._
import org.platanios.tensorflow.api.ops.basic.Basic
import org.platanios.tensorflow.api.ops.control_flow.ControlFlow
import org.platanios.tensorflow.api.ops.math.Math
import org.platanios.tensorflow.api.ops.training.optimizers.schedules.{FixedSchedule, Schedule}
import org.platanios.tensorflow.api.ops.variables.Variable

/** Optimizer that implements the Yogi optimization algorithm.
  *
  * Initialization:
  * {{{
  *   m_0 = 0  // Initialize the 1st moment vector
  *   v_0 = 0  // Initialize the 2nd moment vector
  *   t = 0    // Initialize the time step
  * }}}
  *
  * The Adam update for step `t` is as follows:
  * {{{
  *   learningRate_t = initialLearningRate * sqrt(beta1 - beta2^t) / (1 - beta1^t)
  *   m_t = beta1 * m_{t-1} + (1 - beta1) * gradient
  *   v_t = beta2 * v_{t-1} + (1 - beta2) * gradient * gradient * sign(gradient * gradient - v_{t-1})
  *   variable -= learningRate_t * m_t / (sqrt(v_t) + epsilon)
  * }}}
  *
  * For more information on this algorithm, please refer to this
  * [paper](https://papers.nips.cc/paper/8186-adaptive-methods-for-nonconvex-optimization.pdf).
  *
  * @param  learningRate                Learning rate. Must be `> 0`. If used with `decay`, then this argument
  *                                     specifies the initial value of the learning rate.
  * @param  decay                       Learning rate decay method to use for each update.
  * @param  weightDecay                 Weight decay rate. Note that this is not equivalent to L2 regularization.
  *                                     Instead, the implementation is based on this
  *                                     [paper](https://arxiv.org/abs/1711.05101).
  * @param  excludeFromWeightDecayNames Variables whose name contains any of the strings in this set will be excluded
  *                                     from weight decay regularization.
  * @param  beta1                       Exponential decay rate for the first moment estimates.
  * @param  beta2                       Exponential decay rate for the second moment estimates.
  * @param  useNesterov                 If `true`, Nesterov momentum is used for the updates.
  * @param  epsilon                     Small constant used for numerical stability. This epsilon corresponds to
  *                                     "epsilon hat" in the Kingma and Ba paper (in the formula just before Section
  *                                     2.1), and not to the epsilon in Algorithm 1 of the paper.
  * @param  useLocking                  If `true`, the gradient descent updates will be protected by a lock. Otherwise,
  *                                     the behavior is undefined, but may exhibit less contention.
  * @param  learningRateSummaryTag      Optional summary tag name to use for the learning rate value. If `null`, no
  *                                     summary is created for the learning rate. Otherwise, a scalar summary is created
  *                                     which can be monitored using TensorBoard.
  * @param  name                        Name for this optimizer.
  *
  * @author Emmanouil Antonios Platanios
  */
class Yogi protected (
    val learningRate: Float = 0.001f,
    val decay: Schedule[Float] = FixedSchedule[Float](),
    val weightDecay: Float = 0.0f,
    val excludeFromWeightDecayNames: Set[String] = Set.empty,
    val beta1: Float = 0.9f,
    val beta2: Float = 0.999f,
    val useNesterov: Boolean = false,
    val epsilon: Float = 1e-8f,
    val useLocking: Boolean = false,
    val learningRateSummaryTag: String = null,
    val name: String = "Yogi"
) extends Optimizer {
  override val ignoreDuplicateSparseIndices: Boolean = true

  protected var learningRateTensor: Output[Float] = _
  protected var weightDecayTensor : Output[Float] = _
  protected var beta1Tensor       : Output[Float] = _
  protected var beta2Tensor       : Output[Float] = _
  protected var epsilonTensor     : Output[Float] = _

  protected def getLearningRate[V: TF, I: TF : IsIntOrLong](
      variable: Variable[V],
      iteration: Option[Variable[I]]
  ): Output[V] = {
    if (learningRateTensor == null)
      throw new IllegalStateException("Method 'prepare' has not been called on this optimizer.")
    learningRateTensor.castTo[V].toOutput
  }

  protected def getWeightDecay[V: TF](variable: Variable[V]): Output[V] = {
    if (weightDecayTensor == null)
      throw new IllegalStateException("Method 'prepare' has not been called on this optimizer.")
    weightDecayTensor.castTo[V].toOutput
  }

  protected def getBeta1[V: TF](variable: Variable[V]): Output[V] = {
    if (beta1Tensor == null)
      throw new IllegalStateException("Method 'prepare' has not been called on this optimizer.")
    beta1Tensor.castTo[V].toOutput
  }

  protected def getBeta2[V: TF](variable: Variable[V]): Output[V] = {
    if (beta2Tensor == null)
      throw new IllegalStateException("Method 'prepare' has not been called on this optimizer.")
    beta2Tensor.castTo[V].toOutput
  }

  protected def getEpsilon[V: TF](variable: Variable[V]): Output[V] = {
    if (epsilonTensor == null)
      throw new IllegalStateException("Method 'prepare' has not been called on this optimizer.")
    epsilonTensor.castTo[V].toOutput
  }

  protected def getBetaPowerAccumulators: (Variable[Float], Variable[Float]) = {
    (getNonSlotVariable[Float]("Beta1Power", Op.currentGraph),
        getNonSlotVariable[Float]("Beta2Power", Op.currentGraph))
  }

  override def createSlots(variables: Seq[Variable[Any]]): Unit = {
    // Create slots for the first and second moments.
    variables.foreach(v => {
      zerosSlot("M", v, name)(TF.fromDataType(v.dataType))
      zerosSlot("V", v, name)(TF.fromDataType(v.dataType))
    })
    // We create the 'beta1' and 'beta2' accumulators on the same device as the first variable. We sort the variables
    // list to make sure this device is consistent across workers (these need to go on the same parameter server,
    // otherwise some updates are silently ignored).
    val firstVariable = variables.minBy(_.name)
    getOrCreateNonSlotVariable("Beta1Power", beta1, Set(firstVariable.op), ignoreExisting = true)
    getOrCreateNonSlotVariable("Beta2Power", beta2, Set(firstVariable.op), ignoreExisting = true)
  }

  override def prepare[I: TF : IsIntOrLong](
      iteration: Option[Variable[I]]
  ): Unit = {
    learningRateTensor = decay(Basic.constant(learningRate, name = "LearningRate"), iteration)
    if (learningRateSummaryTag != null)
      Summary.scalar(learningRateSummaryTag, learningRateTensor)
    weightDecayTensor = Basic.constant(weightDecay, name = "WeightDecay")
    beta1Tensor = Basic.constant(beta1, name = "Beta1")
    beta2Tensor = Basic.constant(beta2, name = "Beta2")
    epsilonTensor = Basic.constant(epsilon, name = "Epsilon")
  }

  override def applyDense[T: TF : IsNotQuantized, I: TF : IsIntOrLong](
      gradient: Output[T],
      variable: Variable[T],
      iteration: Option[Variable[I]]
  ): UntypedOp = {
    Op.nameScope(s"$name/ApplyDense") {
      val m = getSlot[T, T]("M", variable)
      val v = getSlot[T, T]("V", variable)
      val (beta1Power, beta2Power) = getBetaPowerAccumulators
      val beta1 = getBeta1(variable)
      val beta2 = getBeta2(variable)
      val epsilon = getEpsilon(variable)
      var learningRate = getLearningRate(variable, iteration)

      val weightDecay = getWeightDecay(variable)
      val weightDecayOp = {
        if (doWeightDecay(variable)) {
          variable.assignSub(learningRate * weightDecay * variable.value).op
        } else {
          ControlFlow.noOp()
        }
      }

      Op.createWith(controlDependencies = Set(weightDecayOp)) {
        val one = Basic.ones[T](Shape())
        learningRate = learningRate * Math.sqrt(one - beta2Power.value.castTo[T])
        learningRate = learningRate / (one - beta1Power.value.castTo[T])

        // m_t = beta1 * m + (1 - beta1) * gradient
        val mScaledGradient = gradient * (one - beta1)
        val mT = m.assign((m.value * beta1) + mScaledGradient)

        // v_t = beta2 * v + (1 - beta2) * gradient * gradient * sign(gradient * gradient - v)
        val gradientSquared = Math.square(gradient)
        val sign = Math.sign(gradientSquared - v.value)
        val vScaledGradient = gradientSquared * (one - beta2) * sign
        val vT = v.assignAdd(vScaledGradient)

        val vTSqrt = Math.sqrt(vT)
        val update = variable.assignSub(learningRate * mT / Math.add(vTSqrt, epsilon))
        ControlFlow.group(Set(update.op, mT.op, vT.op))
      }
    }
  }

  override def applySparse[T: TF : IsNotQuantized, I: TF : IsIntOrLong](
      gradient: OutputIndexedSlices[T],
      variable: Variable[T],
      iteration: Option[Variable[I]]
  ): UntypedOp = {
    Op.nameScope(s"$name/ApplySparse") {
      val m = getSlot[T, T]("M", variable)
      val v = getSlot[T, T]("V", variable)
      val (beta1Power, beta2Power) = getBetaPowerAccumulators
      val beta1 = getBeta1(variable)
      val beta2 = getBeta2(variable)
      val epsilon = getEpsilon(variable)
      var learningRate = getLearningRate(variable, iteration)

      // Apply weight decay, if needed.
      val weightDecay = getWeightDecay(variable)
      val weightDecayOp = {
        if (doWeightDecay(variable)) {
          variable.assignScatterSub(
            gradient.indices,
            learningRate * weightDecay * variable.gather(gradient.indices),
          ).op
        } else {
          ControlFlow.noOp()
        }
      }

      Op.createWith(controlDependencies = Set(weightDecayOp)) {
        val one = Basic.ones[T](Shape())
        learningRate = learningRate * Math.sqrt(one - beta2Power.value.castTo[T])
        learningRate = learningRate / (one - beta1Power.value.castTo[T])

        // m_t = beta1 * m + (1 - beta1) * gradient
        val mScaledGradient = gradient.values * (one - beta1)
        var mT = m.assign(m.value * beta1)
        mT = Op.createWith(controlDependencies = Set(mT.op)) {
          m.assignScatterAdd(gradient.indices, mScaledGradient)
        }

        // v_t = beta2 * v + (1 - beta2) * gradient * gradient * sign(gradient * gradient - v)
        val gradientSquared = Math.square(gradient.values)
        val sign = Math.sign(gradientSquared - v.value.gather(gradient.indices))
        val vScaledGradient = gradientSquared * (one - beta2) * sign
        var vT = v.value
        vT = Op.createWith(controlDependencies = Set(vT.op)) {
          v.assignScatterAdd(gradient.indices, vScaledGradient)
        }

        val vTSqrt = Math.sqrt(vT)
        val update = variable.assignSub(learningRate * mT / Math.add(vTSqrt, epsilon))
        ControlFlow.group(Set(update.op, mT.op, vT.op))
      }
    }
  }

  override def finish(
      updateOps: Set[UntypedOp],
      nameScope: String
  ): UntypedOp = {
    // Update the power accumulators.
    val (beta1Power, beta2Power) = getBetaPowerAccumulators
    val updateBetaPowerOps = Op.createWith(controlDependencies = updateOps) {
      Op.colocateWith(Set(beta1Power.op), ignoreExisting = true) {
        val updateBeta1Power = beta1Power.assign(beta1Power.value * beta1Tensor)
        val updateBeta2Power = beta2Power.assign(beta2Power.value * beta2Tensor)
        Set(updateBeta1Power.op, updateBeta2Power.op)
      }
    }
    ControlFlow.group(updateOps ++ updateBetaPowerOps, nameScope)
  }

  protected def doWeightDecay[V: TF](variable: Variable[V]): Boolean = {
    weightDecay > 0 && excludeFromWeightDecayNames.forall(!variable.name.contains(_))
  }
}

object Yogi {
  def apply(
      learningRate: Float = 0.001f,
      decay: Schedule[Float] = FixedSchedule[Float](),
      weightDecay: Float = 0.0f,
      excludeFromWeightDecayNames: Set[String] = Set.empty,
      beta1: Float = 0.9f,
      beta2: Float = 0.999f,
      useNesterov: Boolean = false,
      epsilon: Float = 1e-8f,
      useLocking: Boolean = false,
      learningRateSummaryTag: String = null,
      name: String = "Yogi"
  ): Yogi = {
    new Yogi(
      learningRate, decay, weightDecay, excludeFromWeightDecayNames, beta1, beta2,
      useNesterov, epsilon, useLocking, learningRateSummaryTag, name)
  }
}
