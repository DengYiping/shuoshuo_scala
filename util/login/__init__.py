# -*- coding: utf-8 -*-

if __name__ == '__main__':
    import tea
else:
    from . import tea

import requests, re, os, tempfile
import base64, hashlib, rsa, binascii


class QQ:
    appid       = 549000912
    urlLogin    = 'http://xui.ptlogin2.qq.com/cgi-bin/xlogin'
    urlCheck    = 'http://check.ptlogin2.qq.com/check'
    urlCap      = 'http://captcha.qq.com/cap_union_show'
    urlImg      = 'http://captcha.qq.com/getimgbysig'
    urlSubmit   = 'http://ptlogin2.qq.com/login'
    urlBlog     = 'http://user.qzone.qq.com/p/b1/cgi-bin/blognew/get_abs'
    urlFeed     = 'http://taotao.qzone.qq.com/cgi-bin/emotion_cgi_publish_v6?g_tk=%d'

    def __init__(self, qq, pwd):
        self.qq          = qq
        self.pwd         = pwd
        self.nickname    = None
        self.vcode       = None
        self.session     = None
        self.vcodeShow   = 0
        self.loginSig    = None
        self.requests    = requests.Session()
        par = {
            'proxy_url'         : 'http://qzs.qq.com/qzone/v6/portal/proxy.html',
            'daid'              : 5,
            'hide_title_bar'    : 1,
            'low_login'         : 0,
            'qlogin_auto_login' : 1,
            'no_verifyimg'      : 1,
            'link_target'       : 'blank',
            'appid'             : self.appid,
            'style'             : 22,
            'target'            : 'self',
            's_url'             : 'http://qzs.qq.com/qzone/v5/loginsucc.html?para=izone',
            'pt_qr_app'         : '手机QQ空间',
            'pt_qr_link'        : 'http://z.qzone.com/download.html',
            'self_regurl'       : 'http://qzs.qq.com/qzone/v6/reg/index.html',
            'pt_qr_help_link'   : 'http://z.qzone.com/download.html',
        }
        r = self.requests.get(self.urlLogin, params=par);
        for x in r.cookies:
            if x.name == 'pt_login_sig':
                self.loginSig = x.value
                break
        self.check()

    def check(self):
        par = {
            'regmaster'     : '',
            'pt_tea'        : 1,
            'pt_vcode'      : 1,
            'uin'           : self.qq,
            'appid'         : self.appid,
            'js_ver'        : 10143,
            'js_type'       : 1,
            'login_sig'     : self.loginSig,
            'u1'            : 'http://qzs.qq.com/qzone/v5/loginsucc.html?para=izone',
            'r'             : '0.019801550777629018'
        }
        r = self.requests.get(self.urlCheck, params=par)
        li = re.findall('\'(.*?)\'', r.text)
        self.vcode = li[1]
        if li[0] == '1':
            self.vcodeShow = 1
            self.getVerifyCode()
        else:
            self.session = li[3]
        self.login()

    def getEncryption(self):
        puk = rsa.PublicKey(int(
            'F20CE00BAE5361F8FA3AE9CEFA495362'
            'FF7DA1BA628F64A347F0A8C012BF0B25'
            '4A30CD92ABFFE7A6EE0DC424CB6166F8'
            '819EFA5BCCB20EDFB4AD02E412CCF579'
            'B1CA711D55B8B0B3AEB60153D5E0693A'
            '2A86F3167D7847A0CB8B00004716A909'
            '5D9BADC977CBB804DBDCBA6029A97108'
            '69A453F27DFDDF83C016D928B3CBF4C7',
            16
        ), 3)
        e = int(self.qq).to_bytes(8, 'big')
        o = hashlib.md5(self.pwd.encode())
        r = bytes.fromhex(o.hexdigest())
        p = hashlib.md5(r + e).hexdigest()
        a = binascii.b2a_hex(rsa.encrypt(r, puk)).decode()
        s = hex(len(a) // 2)[2:]
        l = binascii.hexlify(self.vcode.upper().encode()).decode()
        c = hex(len(l) // 2)[2:]
        c = '0' * (4 - len(c)) + c
        s = '0' * (4 - len(s)) + s
        salt = s + a + binascii.hexlify(e).decode() + c + l
        return base64.b64encode(
            tea.encrypt(bytes.fromhex(salt), bytes.fromhex(p))
        ).decode().replace('/', '-').replace('+', '*').replace('=', '_')

    def login(self):
        d = self.requests.cookies.get_dict()
        if 'ptvfsession' in d:
            self.session = d['ptvfsession']

        par = {
            'action'                : '2-0-1450538632070',
            'aid'                   : self.appid,
            'daid'                  : 5,
            'from_ui'               : 1,
            'g'                     : 1,
            'h'                     : 1,
            'js_type'               : 1,
            'js_ver'                : 10143,
            'login_sig'             : self.loginSig,
            'p'                     : self.getEncryption(),
            'pt_randsalt'           : 0,
            'pt_uistyle'            : 32,
            'pt_vcode_v1'           : self.vcodeShow,
            'pt_verifysession_v1'   : self.session,
            'ptlang'                : 2052,
            'ptredirect'            : 0,
            't'                     : 1,
            'u'                     : self.qq,
            'u1'                    : 'http://qzs.qq.com/qzone/v5/loginsucc.html?para=izone',
            'verifycode'            : self.vcode
        }
        r = self.requests.get(self.urlSubmit, params=par)
        li = re.findall('http://[^\']+', r.text)
        if len(li):
            self.urlQzone = li[0]
        print(r.text)


    def getVerifyCode(self):
        par = {
            'clientype' : 2,
            'uin'       : self.qq,
            'aid'       : self.appid,
            'cap_cd'    : self.vcode,
            'pt_style'  : 32,
            'rand'      : 0.5994133797939867,
        }
        r = self.requests.get(self.urlCap, params=par)
        vsig = re.findall('g_vsig = "([^"]+)"', r.text)[0]
        par = {
            'clientype'     : 2,
            'uin'           : self.qq,
            'aid'           : self.appid,
            'cap_cd'        : self.vcode,
            'pt_style'      : 32,
            'rand'          : 0.5044117101933807,
            'sig'           : vsig
        }
        r = self.requests.get(self.urlImg, params=par)
        tmp = tempfile.mkstemp(suffix = '.jpg')
        os.write(tmp[0], r.content)
        os.close(tmp[0])
        os.startfile(tmp[1])
        vcode = input('Verify code: ')
        os.remove(tmp[1])
        # return vcode
        par = {
            'clientype'     : 2,
            'uin'           : self.qq,
            'aid'           : self.appid,
            'cap_cd'        : self.vcode,
            'pt_style'      : 32,
            'rand'          : '0.9467124678194523',
            'capclass'      : 0,
            'sig'           : vsig,
            'ans'           : vcode,
        }
        r = self.requests.get('http://captcha.qq.com/cap_union_verify_new', params=par)
        js = r.json()
        self.vcode = js['randstr']
        self.session = js['ticket']

    def gtk(self):
        d = self.requests.cookies.get_dict()
        hash = 5381
        s = ''
        if 'p_skey' in d:
            s = d['p_skey']
        elif 'skey' in d:
            s = d['skey']
        for c in s:
            hash += (hash << 5) + ord(c)
        return hash & 0x7fffffff

    def blog(self):
        par = {
            'hostUin'   : self.qq,
            'uin'       : self.qq,
            'blogType'  : 0,
            'cateName'  : '',
            'cateHex'   : '',
            'statYear'  : 2015,
            'reqInfo'   : 7,
            'pos'       : 0,
            'num'       : 15,
            'sortType'  : 0,
            'source'    : 0,
            'ref'       : 'qzone',
            'g_tk'      : self.gtk(),
            'verbose'   : 1
        }
        r = self.requests.get(self.urlBlog, params=par)
        return r.text

    def feed(self, s):
        headers = {
            'User-Agent': 'Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.109 Safari/537.36'
        }
        self.requests.get(self.urlQzone, headers=headers)
        par = {
            'syn_tweet_verson'    : 1,
            'paramstr'            : 1,
            'pic_template'        : '',
            'richtype'            : '',
            'richval'             : '',
            'special_url'         : '',
            'subrichtype'         : '',
            'who'                 : 1,
            'con'                 : s,
            'feedversion'         : 1,
            'ver'                 : 1,
            'ugc_right'           : 1,
            'to_tweet'            : 0,
            'to_sign'             : 0,
            'hostuin'             : self.qq,
            'code_version'        : 1,
            'format'              : 'fs'
        }
        headers.update({
            'Host': 'taotao.qzone.qq.com'
        })
        r = self.requests.post(self.urlFeed % self.gtk(), data=par, headers=headers)
        print(r.text)


if __name__ == '__main__':
    qq = QQ('qq', 'password')
    qq.feed('我就是我，颜色不一样的烟火!')

