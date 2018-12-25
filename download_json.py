import json
import urllib
import requests

def codeToImport(input):
    if input == 'PO2':
        return 'P02'
    elif input == 'CON':
        return 'CON_'
    elif input == 'NMS':
        return 'NEM'
    elif input == 'pHHO':
        return 'HHO'
    elif input == 'DD3_DVD':
        return 'DVD'
    elif input == 'DD3_EVG':
        return 'EVG'
    elif input == 'DD3_GVL':
        return 'GVL'
    elif input == 'DD3_JVC':
        return 'JVC'
    elif input == 'FRF_UGIN':
        return 'UGIN'
    elif input == 'MPS_AKH':
        return 'MP2'
    else:
        return input

def codeToJson(input):
    if input == '2ED':
        return 'ed2'
    elif input == '3ED':
        return 'ed3'
    elif input == '4ED':
        return 'ed4'
    elif input == '5ED':
        return 'ed5'
    elif input == '6ED':
        return 'ed6'
    elif input == '7ED':
        return 'ed7'
    elif input == '8ED':
        return 'ed8'
    elif input == '9ED':
        return 'ed9'
    elif input == '10E':
        return 'e10'
    elif input == '5DN':
        return 'dn5'
    elif input == 'MP2':
        return 'mps_akh'
    elif input == 'NEM':
        return 'nms'
    elif input == 'P02':
        return 'po2'
    else:
        return input

content = json.load(open('app/src/debug/res/raw/set_list.json'))

for set in content:
    code = codeToImport(set['code'])
    print ("https://mtgjson.com/json/%s.json" % code)
    url = ("https://mtgjson.com/json/%s.json" % code)
    req = urllib.request.Request(
        url,
        data=None,
        headers={
            'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.47 Safari/537.36'
        }
    )
    with open(("app/src/debug/res/raw/%s_x.json" % codeToJson(code)), 'wb') as img_file:
        img_file.write(urllib.request.urlopen(req).read())