import tensorflow as tf   

try:
  tf.convert_to_tensor(tf.TensorShape(()))
  tf.random_normal(1)
except Exception as e:
  print(e)
