import sys

def recur(n,m):
    if(m <= 0 or n <= 0):
        return 0
    if(m == 1):
        return 1
    return recur(n - 1,m)  * recur(1,m) + (recur(n, m -1) + recur(n, m - 2) +  recur(n, m - 3) + recur(n, m -4))










len = int(sys.stdin.readline().strip())

for _ in range(len):
    n,m = [int(i) for i in sys.stdin.readline().strip().split()]
    res = recur(n,m)
    print(res)




