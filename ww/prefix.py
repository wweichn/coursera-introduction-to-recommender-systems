import sys
s=sys.stdin.readline().strip()

def find(s1,s2):
    len1 = len(s2)
    if(s1[0:len1] == s2):
        return True
    else:
        return False

def f(x):
    return len(x)

max_len = 0
max_prefix = ""
cur_len = 0
strings = s.split(',')
s_new = sorted(strings,key = f,reverse=True)
for i in range(len(strings)):
    if(len(s_new[i]) > max_len):
        for j in range(i + 1,len(strings)):
            if(find(s_new[i],s_new[j])):
                cur_len = len(s_new[j])
                if(cur_len > max_len):
                    max_len = cur_len
                    max_prefix = s_new[j]
                    break;

print(max_prefix)
