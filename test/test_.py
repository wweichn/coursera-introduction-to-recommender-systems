import sys
line=sys.stdin.readline().strip()
n,k=[int(i) for i in line.split()]
line=sys.stdin.readline().strip()
s=[int(i) for i in line.split()]
def find(s):
    ma,mi=0,0
    for i in range(1,len(s)):
        if s[i]>s[ma]:
            ma=i
        if s[i]<s[mi]:
            mi=i
    return ma,mi

def output(res):
    for p in res:
        print('%d %d'%(p[0],p[1]))

res=[]
for i in range(k+1):
    ma,mi=find(s)
    if i==0 or ans>s[ma]-s[mi]:
        ans=s[ma]-s[mi]
        ansp=i
    if s[ma]-s[mi]<2 or i==k:
        print(ans,ansp)
        output(res[:ansp])
        break
    res.append([ma+1,mi+1])
    s[ma]-=1
    s[mi]+=1
    print(s)