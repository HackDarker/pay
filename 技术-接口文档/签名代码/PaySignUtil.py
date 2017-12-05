#!/usr/bin/env python
# -*- coding: UTF-8 -*-
#
# 获得加密后的字符串

import hashlib

class JinFuEncrypt(object):

    def __init__(self, scrStr=None):
        if scrStr is None:
            return

    def getDecretStr(self,cryKey, mapSender):
        """
        encKey: 加解密的key, 例如 u4smNesRMrDAIU62HXNy1eoeP9uD8yaUKCcd103j
        mapSender:要加密的数据, 用map 封装
        return: 加密后的字符串
        """
        allKeys = mapSender.keys()
        allKeys.sort()

        srcStr = "keyValue="
        srcStr += cryKey

        for oneKey in allKeys:
            oneValue = mapSender[oneKey]
            oneArg = "&" + oneKey + "=" + oneValue
            srcStr += oneArg

        srcStr = srcStr.upper()
        return self.get_md5_value(srcStr)

    def get_md5_value(self,srcStr):
        myMd5 = hashlib.md5()
        myMd5.update(srcStr)
        myMd5_Digest = myMd5.hexdigest()
        return myMd5_Digest