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

package org.platanios.tensorflow.api.ops

import org.platanios.tensorflow.api.core.Shape
import org.platanios.tensorflow.api.core.types._
import org.platanios.tensorflow.api.implicits.Implicits._
import org.platanios.tensorflow.api.ops.basic.Basic
import org.platanios.tensorflow.api.ops.math.Math
import org.platanios.tensorflow.api.utilities.DefaultsTo.IntDefault

/** Contains functions for constructing ops related to clipping tensor values.
  *
  * @author Emmanouil Antonios Platanios
  */
trait Clip {
  /** $OpDocClipClipByValue
    *
    * @group ClipOps
    * @param  input        Input tensor.
    * @param  clipValueMin 0-D (scalar) tensor, or a tensor with the same shape as `input`, specifying the minimum value
    *                      to clip by.
    * @param  clipValueMax 0-D (scalar) tensor, or a tensor with the same shape as `input`, specifying the maximum value
    *                      to clip by.
    * @param  name         Name prefix for created ops.
    * @return Created op output.
    */
  def clipByValue[T: TF : IsNotQuantized](
      input: Output[T],
      clipValueMin: Output[T],
      clipValueMax: Output[T],
      name: String = "ClipByValue"
  ): Output[T] = {
    Op.Builder[(Output[T], Output[T], Output[T]), Output[T]](
      opType = "ClipByValue",
      name = name,
      input = (input, clipValueMin, clipValueMax)
    ).setGradientFn(clipByValueGradient(_, _)(TF[T], IsNotQuantized[T]))
        .build().output
  }

  protected def clipByValueGradient[T: TF : IsNotQuantized](
      op: Op[(Output[T], Output[T], Output[T]), Output[T]],
      outputGradient: Output[T]
  ): (Output[T], Output[T], Output[T]) = {
    val x = op.input._1
    val y = op.input._2
    val z = op.input._3
    val xShape = Basic.shape(x)
    val yShape = Basic.shape(y)
    val zShape = Basic.shape(z)
    val zeros = Basic.zerosLike(outputGradient)
    val xyMask = Math.less(x, y)
    val xzMask = Math.greater(x, z)
    val xGradient = Math.select(Math.logicalOr(xyMask, xzMask), zeros, outputGradient)
    val yGradient = Math.select(xyMask, outputGradient, zeros)
    val zGradient = Math.select(xzMask, outputGradient, zeros)
    val (_, ry) = Basic.broadcastGradientArguments(xShape, yShape)
    val (rx, rz) = Basic.broadcastGradientArguments(xShape, zShape)
    (Math.sum(xGradient, rx).reshape(xShape),
        Math.sum(yGradient, ry).reshape(yShape),
        Math.sum(zGradient, rz).reshape(zShape))
  }

  /** $OpDocClipClipByNorm
    *
    * @group ClipOps
    * @param  input    Input tensor.
    * @param  clipNorm 0-D (scalar) tensor > 0, specifying the maximum clipping value.
    * @param  axes     1-D (vector) tensor containing the dimensions to use for computing the l2-norm. If
    *                  `null` (the default), all dimensions are used.
    * @param  name     Name prefix for created ops.
    * @return Created op output.
    */
  def clipByNorm[T: TF : IsNotQuantized, I: IntDefault : TF : IsIntOrLong](
      input: Output[T],
      clipNorm: Output[T],
      axes: Output[I] = null,
      name: String = "ClipByNorm"
  ): Output[T] = {
    Op.nameScope(name) {
      // Calculate the l2-norm and clip elements by the ratio of `clipNorm` to that l2-norm.
      val l2Norm = Math.sum(Math.square(input), axes, keepDims = true).sqrt
      val intermediate = input * clipNorm
      // Assert that the result shape is compatible with the initial shape, to prevent unintentional broadcasting.
      input.shape.assertIsCompatibleWith(intermediate.shape)
      Basic.identity(intermediate / Math.maximum(l2Norm, clipNorm))
    }
  }

  /** $OpDocClipClipByAverageNorm
    *
    * @group ClipOps
    * @param  input    Input tensor.
    * @param  clipNorm 0-D (scalar) tensor > 0, specifying the maximum clipping value.
    * @param  name     Name prefix for created ops.
    * @return Created op output.
    */
  def clipByAverageNorm[T: TF : IsNotQuantized](
      input: Output[T],
      clipNorm: Output[T],
      name: String = "ClipByAverageNorm"
  ): Output[T] = {
    Op.nameScope(name) {
      // Calculate the l2-norm per element and clip elements by the ratio of `clipNorm` to that l2-norm.
      val numElements = Basic.size(input).castTo[T]
      val l2NormInv = Math.sum(input.square, keepDims = true).rsqrt
      val intermediate = input * clipNorm
      // Assert that the result shape is compatible with the initial shape, to prevent unintentional broadcasting.
      input.shape.assertIsCompatibleWith(intermediate.shape)
      val one = Basic.ones[T](Shape())
      Basic.identity(intermediate * Math.minimum(l2NormInv * numElements, one / clipNorm))
    }
  }

  /** $OpDocClipGlobalNorm
    *
    * @group ClipOps
    * @param  inputs Input tensors.
    * @param  name   Name prefix for created ops.
    * @return Created op output.
    */
  def globalNorm[T: TF : IsDecimal](
      inputs: Seq[OutputLike[T]],
      name: String = "GlobalNorm"
  ): Output[T] = {
    Op.nameScope(name) {
      val values = inputs.collect {
        case o: Output[T] => o
        case o: OutputIndexedSlices[T] => o.values
        case o: SparseOutput[T] => o.values
      }
      val halfSquaredNorms = values.filter(_ != null).map(v => Op.colocateWith(Set(v.op), ignoreExisting = true) {
        NN.l2Loss(v)
      })
      val halfSquaredNorm = Math.sum(Basic.stack(halfSquaredNorms))
      val two = Basic.constant(2).castTo[T]
      Math.sqrt(halfSquaredNorm * two)
    }
  }

  /** $OpDocClipClipByGlobalNorm
    *
    * @group ClipOps
    * @param  inputs     Input tensors.
    * @param  clipNorm   0-D (scalar) tensor > 0, specifying the maximum clipping value.
    * @param  globalNorm 0-D (scalar) tensor containing the global norm to use. If not provided, `globalNorm()` is used
    *                    to compute the norm.
    * @param  name       Name prefix for created ops.
    * @return Tuple containing the clipped tensors as well as the global norm that was used for the clipping.
    */
  def clipByGlobalNorm[T: TF : IsDecimal](
      inputs: Seq[OutputLike[T]],
      clipNorm: Output[T],
      globalNorm: Output[T] = null,
      name: String = "ClipByGlobalNorm"
  ): (Seq[OutputLike[T]], Output[T]) = {
    Op.nameScope(name) {
      val norm = if (globalNorm != null) globalNorm else this.globalNorm(inputs)
      val one = Basic.ones[T](Shape())
      // Calculate the l2-norm and clip elements by the ratio of `clipNorm` to that l2-norm.
      val scale = clipNorm * Math.minimum(Math.divide(one, norm), Math.divide(one, clipNorm))
      val values = inputs.map {
        case null => null
        case o: Output[T] => o
        case o: OutputIndexedSlices[T] => o.values
        case o: SparseOutput[T] => o.values
      }
      val clippedValues = values.map(value => {
        if (value == null)
          null
        else
          Op.colocateWith(Set(value.op), ignoreExisting = true)(Basic.identity(value * scale))
      })
      val result = inputs.zip(clippedValues).map {
        case (_: Output[T], c: Output[T]) => c
        case (i: OutputIndexedSlices[T], c: Output[T]) => OutputIndexedSlices(i.indices, c, i.denseShape)
        case (i: SparseOutput[T], c: Output[T]) => SparseOutput(i.indices, c, i.denseShape)
        case _ => null // Impossible case.
      }
      (result, norm)
    }
  }
}

object Clip extends Clip {
  /** @define OpDocClipClipByValue
    *   The `clipByValue` op clips tensor values to a specified min and max value.
    *
    *   Given a tensor `input`, the op returns a tensor of the same type and shape as `input`, with its values clipped
    *   to `clipValueMin` and `clipValueMax`. Any values less than `clipValueMin` are set to `clipValueMin` and any
    *   values greater than `clipValueMax` are set to `clipValueMax`.
    *
    * @define OpDocClipClipByNorm
    *   The `clipByNorm` op clips tensor values to a specified maximum l2-norm value.
    *
    *   Given a tensor `input`, and a maximum clip value `clipNorm`, the op normalizes `input` so that its l2-norm is
    *   less than or equal to `clipNorm`, along the dimensions provided in `axes`. Specifically, in the default case
    *   where all dimensions are used for the calculation, if the l2-norm of `input` is already less than or equal to
    *   `clipNorm`, then `input` is not modified. If the l2-norm is greater than `clipNorm`, then the op returns a
    *   tensor of the same data type and shape as `input`, but with its values set to
    *   `input * clipNorm / l2Norm(input)`.
    *
    *   In this case, the l2-norm of the output tensor is equal to `clipNorm`.
    *
    *   As another example, if `input` is a matrix and `axes == [1]`, then each row of the output will have l2-norm
    *   equal to `clipNorm`. If `axes == [0]` instead, each column of the output will be clipped.
    *
    *   This op is typically used to clip gradients before applying them with an optimizer.
    *
    * @define OpDocClipClipByAverageNorm
    *   The `clipByAverageNorm` op clips tensor values to a specified maximum average l2-norm value.
    *
    *   Given a tensor `input`, and a maximum clip value `clipNorm`, the op normalizes `input` so that its average
    *   l2-norm is less than or equal to `clipNorm`. If the average l2-norm of `input` is already less than or equal to
    *   `clipNorm`, then `input` is not modified. If the l2-norm is greater than `clipNorm`, then the op returns a
    *   tensor of the same data type and shape as `input`, but with its values set to
    *   `input * clipNorm / l2NormAvg(input)`.
    *
    *   In this case, the average l2-norm of the output tensor is equal to `clipNorm`.
    *
    *   This op is typically used to clip gradients before applying them with an optimizer.
    *
    * @define OpDocClipGlobalNorm
    *   The `globalNorm` op computes the global norm of multiple tensors.
    *
    *   Given a sequence of tensors `inputs`, the op returns the global norm of the elements in all tensors in `inputs`.
    *   The global norm is computed as `globalNorm = sqrt(sum(inputs.map(i => l2Norm(i)^2)))`.
    *
    *   Any entries in `inputs` that are `null` are ignored.
    *
    * @define OpDocClipClipByGlobalNorm
    *   The `clipByGlobalNorm` op clips values of multiple tensors by the ratio of the sum of their norms.
    *
    *   Given a sequence of tensors `inputs`, and a clipping ratio `clipNorm`, the op returns a sequence of clipped
    *   tensors `clipped`, along with the global norm (`globalNorm`) of all tensors in `inputs`. Optionally, if you've
    *   already computed the global norm for `inputs`, you can specify the global norm with `globalNorm`.
    *
    *   To perform the clipping, the values `inputs(i)` are set to: `inputs(i) * clipNorm / max(globalNorm, clipNorm)`,
    *   where: `globalNorm = sqrt(sum(inputs.map(i => l2Norm(i)^2)))`.
    *
    *   If `clipNorm > globalNorm` then the tensors in `inputs` remain as they are. Otherwise, they are all shrunk by
    *   the global ratio.
    *
    *   Any of the tensors in `inputs` that are `null` are ignored.
    *
    *   Note that this is generally considered as the "correct" way to perform gradient clipping (see, for example,
    *   [Pascanu et al., 2012](http://arxiv.org/abs/1211.5063)). However, it is slower than `clipByNorm()` because all
    *   the input tensors must be ready before the clipping operation can be performed.
    */
  private[ops] trait Documentation
}
