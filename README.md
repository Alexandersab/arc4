# 功能描述
RC4加解密功能， Python3 跟Java实现， 可以很方便的实现Python加密后的文本Java解密等， 支持非UTF-8字符处理  
主要应对一些数据处理的场景 比如 Python3 存取数据，对数据进行加密处理，然后Java做业务逻辑处理的时候， 需要解密， 这就要求Python跟Java实现的加解密逻辑保持一致， 而且在中文处理中特定业务下存在非UTF-8的字符，给解密过程造成了一定的困扰， 如:`天津辉𤾗图书有限公司`我这里实现的代码兼容了这些情况。
支持的处理情况:
+ Python加密， Python解密
+ Python加密， Java解密
+ Java加密， Java解密
+ Java加密， Python解密

# 环境配置
不论Python3 还是Java， 代码中都使使用的是:从环境变量中读取RC4KEY的值作为秘钥，
这么处理是为了兼容同一个环境中的 Python3 跟 Java 代码， 避免了在 Python3 跟 Java 中都需要实现从配置文件或者DB中读取秘钥的情况， 这里可以根据自己的实际需要酌情处理， 可以配置环境变量 RC4KEY=你的真实秘钥 ，也可以改写从DB或者配置文件中解析

# API
## Java
```java
// 加密
RC4Utils.encrypt(str)
// 解密
RC4Utils.decrypt(str)
```
## Python3
```python
import rc4.encrypt, decrypt

# 加密
encrpyt(str)
# 解密
decrypt(str)
```
