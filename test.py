import tensorflow as tf   
import traceback
try:
  tf.convert_to_tensor(tf.TensorShape(()))
  tf.random_normal(1)
except Exception as e:
  # traceback.print_exc()
  print("Traceback (most recent call last):
  File "test.py", line 5, in <module>
    tf.random_normal(1)
  File "/usr/local/lib/python2.7/dist-packages/tensorflow/python/ops/random_ops.py", line 79, in random_normal
    shape_tensor, dtype, seed=seed1, seed2=seed2)
  File "/usr/local/lib/python2.7/dist-packages/tensorflow/python/ops/gen_random_ops.py", line 728, in random_standard_normal
    seed2=seed2, name=name)
  File "/usr/local/lib/python2.7/dist-packages/tensorflow/python/framework/op_def_library.py", line 788, in _apply_op_helper
    op_def=op_def)
  File "/usr/local/lib/python2.7/dist-packages/tensorflow/python/util/deprecation.py", line 507, in new_func
    return func(*args, **kwargs)
  File "/usr/local/lib/python2.7/dist-packages/tensorflow/python/framework/ops.py", line 3616, in create_op
    op_def=op_def)
  File "/usr/local/lib/python2.7/dist-packages/tensorflow/python/framework/ops.py", line 2027, in __init__
    control_input_ops)
  File "/usr/local/lib/python2.7/dist-packages/tensorflow/python/framework/ops.py", line 1867, in _create_c_op
    raise ValueError(str(e))
ValueError: Shape must be rank 1 but is rank 0 for 'random_normal/RandomStandardNormal' (op: 'RandomStandardNormal') with input shapes: [].")
  
