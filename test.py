import tensorflow as tf   
import traceback
try:
  tf.convert_to_tensor(tf.TensorShape(()))
  tf.random_normal(1)
except Exception as e:
  traceback.print_exc(file=open('/script/tensorflow_scala18-buggy.txt','w+'))
  
