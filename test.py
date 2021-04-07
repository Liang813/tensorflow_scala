import tensorflow as tf   
import traceback
try:
  tf.convert_to_tensor(tf.TensorShape(()))
  tf.random_normal(1)
except Exception as e:
  print(traceback.print_exc())
  
