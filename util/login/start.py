from qqloginlib import QQ
import requests
def addqq(baseURL, qq, pwd):
    qq_obj = QQ(qq,pwd)
    dic = qq_obj.requests.cookies.get_dict()
    skey = dic["skey"]
    r = requests.get(baseURL + "?qq=" + qq +"&skey=" + skey)
    return r
qq_num = input("QQ number:")
qq_pwd = input("Password:")
base1 = "http://localhost:8080/admin/addworker/"
base2 = "http://localhost:8080/admin/start/"
addqq(base2, qq_num, qq_pwd)
