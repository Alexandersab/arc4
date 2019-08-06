# -*- coding: utf-8 -*-

import os

# 获取加解密需要的秘钥， 从环境变量读取
_secure_key = os.environ.get('RC4KEY')
assert _secure_key is not None, 'RC3KEY is not in the environmental variables!'
_offset = 256


# 加密信息
def encrypt(raw: str) -> str:
    return bytes(_transform(raw), encoding='UTF-8').hex()


# 解密信息
def decrypt(enc: str) -> str:
    return _transform(bytes.decode(bytes.fromhex(enc), encoding='UTF-8'))


def _init_cypher() -> list:
    cypher = list(range(_offset))
    follow = 0
    for cur in range(_offset):
        follow = (follow + cypher[cur] + ord(_secure_key[cur % len(_secure_key)])) % _offset
        cypher[cur], cypher[follow] = cypher[follow], cypher[cur]
    return cypher


def _transform(message) -> str:
    cypher = _init_cypher()
    res = []
    cur = follow = 0
    for item in message:
        cur = (cur + 1) % _offset
        follow = (follow + cypher[cur]) % _offset
        cypher[cur], cypher[follow] = cypher[follow], cypher[cur]
        res.append(chr(ord(item) ^ cypher[(cypher[cur] + cypher[follow]) % _offset]))
    return "".join(res)
