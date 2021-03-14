import tensorflow as tf   
import tsensor

try:
  tf.convert_to_tensor(tf.TensorShape(()))
  with tsensor.clarify():
    tf.random_normal(1)
except Exception as e:
  print(e)
