import json
from shutil import copyfile

mapCodeToJSON = {'10e': 'e10', '9ed': 'ed9', '8ed': 'ed8', '7ed': 'ed7', '6ed': 'ed6', '5ed': 'ed5',
                 '4ed': 'ed4', '3ed': 'ed3', '2ed': 'ed2', '5dn': 'dn5'}


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


def codeToJSON(input):
    value = mapCodeToJSON.get(input, None)
    if value is None:
        return input
    else:
        return value


content = json.load(open('MTGSearch/src/debug/res/raw/set_list.json'))

for set in content:
    code = codeToImport(set['code'])
    input = '../mtgjson4/set_outputs/%s.json' % code
    output = 'MTGSearch/src/debug/res/raw/%s_x.json' % codeToJSON(set['code'].lower())
    print output
    try:
        copyfile(input, output)
        # file = open(input)
        print "%s copied to %s" % (input, output)
    except Exception, e:
        print code
        print e
        print "%s failed to copy" % input

# print content[1]['code']
# input = '../mtgjson4/set_outputs/%s.json' % content[1]['code']
# output = 'MTGSearch/src/debug/res/raw/%s_x.json' % content[1]['code'].lower()
# print input
# print output
# copyfile(input, output)
# file = open(input)
# print file
