f = open('test.txt','r')
out = open('out.txt','w')

dict = {}
for line in f:
    s1 = line.split()
    key = s1[0]
    value = s1[1:]
    print(key,value)
    if(key in dict.keys()):
        dict[key].extend(value)
    else:
        dict[key] = value
for key in dict.keys():
    out.write(key + '\t'.join(dict[key]) + '\n')
    print(dict[key])