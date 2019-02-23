import csv


score1 = 0
score2 = 0
score3 = 0
score4 = 0
score5 = 0
score6 = 0



count1 = 0
count2 = 0
count3 = 0
count4 = 0
count5 = 0
count6 = 0
'''
with open('eval-results.csv') as f:
    f_csv = csv.DictReader(f)
    for row in f_csv:
        if(row['Algorithm'] == 'SVD' and row['Bias'] == 'global'):
            count1 += 1
            score1 += float(row['TopN.nDCG'])
        elif(row['Algorithm'] == 'SVD' and row['Bias'] == 'item'):
            count2 += 1
            score2 += float(row['TopN.nDCG'])
        elif (row['Algorithm'] == 'SVD' and row['Bias'] == 'user'):
            count3 += 1
            score3 += float(row['TopN.nDCG'])
        elif (row['Algorithm'] == 'SVD' and row['Bias'] == 'user-item'):
            count4 += 1
            score4 += float(row['TopN.nDCG'])
        elif (row['Algorithm'] == 'ItemItem'):
            count5 += 1
            score5 += float(row['TopN.nDCG'])
        elif (row['Algorithm'] == 'PersMean'):
            count6 += 1
            score6 += float(row['TopN.nDCG'])

    pre1 = score1/ count1
    pre2 = score2/ count2
    pre3 = score3/ count3
    pre4 = score4/ count4
    pre5 = score5/ count5
    pre6 = score6/ count6

    print(pre1,pre2,pre3,pre4,pre5,pre6)

'''

with open('eval-results.csv') as f:
    f_csv = csv.DictReader(f)
    for row in f_csv:
        if(row['Algorithm'] == 'SVD' and row['Bias'] == 'global'):
            if(row[])
            count1 += 1
            score1 += float(row['TopN.nDCG'])
        elif(row['Algorithm'] == 'SVD' and row['Bias'] == 'item'):
            count2 += 1
            score2 += float(row['TopN.nDCG'])
        elif (row['Algorithm'] == 'SVD' and row['Bias'] == 'user'):
            count3 += 1
            score3 += float(row['TopN.nDCG'])
        elif (row['Algorithm'] == 'SVD' and row['Bias'] == 'user-item'):
            count4 += 1
            score4 += float(row['TopN.nDCG'])
        elif (row['Algorithm'] == 'ItemItem'):
            count5 += 1
            score5 += float(row['TopN.nDCG'])
        elif (row['Algorithm'] == 'PersMean'):
            count6 += 1
            score6 += float(row['TopN.nDCG'])







