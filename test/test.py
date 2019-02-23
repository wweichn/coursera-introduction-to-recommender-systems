import sys

line = sys.stdin.readline().strip()
n, m = [int(i) for i in line.split()]
a = [[0] * n for _ in range(n)]
for i in range(n):
    s = sys.stdin.readline().strip()
    if len(s) == 0:
        continue
    for j in s.split():
        a[i][int(j)] = 1
        a[int(j)][i] = 1

ans = [-1] * n
for i in range(n):
    if a[m][i] > 0 or m == i:
        continue
    count = 0
    for j in range(n):
        if a[i][j] > 0 and a[j][m] > 0:
            count += 1
    ans[i] = count

if max(ans) < 1:
    print(-1)
else:
    for i in range(n):
        if ans[i] == max(ans):
            print(i)
            break