import pandas as pd
import numpy as np

data = pd.read_csv('./HW1-data.csv')

index = data.columns.tolist()   # column names

# 统计 4,5出现频率

'''
counts = data[index[2:]].apply(pd.value_counts).reset_index()

bb = counts[counts['index'].isin(['4','5'])].sum()

a = bb / counts.sum()
print(a)

'''
#  统计两个电影同时有人观看的次数

'''
list = []
for i in range(2,len(index)):
    list.append(data[[index[i],index[8]]].count(axis = 1).value_counts() / (data[index[8]].count() * data[index[i]].count()))
print(pd.concat(list,axis = 1))
'''

coRelation = []
for name in index[2:]:
    val = np.corrcoef(np.nan_to_num(np.array(data[name], dtype = np.float16)), np.nan_to_num(np.array(data[index[8]],dtype = np.float16)))
    coRelation.append(val[0][1])

print(coRelation)


# female average score - male average score
'''
fe_male = data[index[1:]].groupby(index[1]).sum() / data[index[1:]].groupby(index[1]).count()
#print(fe_male.reset_index())
fe_male.loc['Diff'] = fe_male.loc[0] - fe_male.loc[1]
fe_male.to_csv('./1.csv')
print(fe_male)

fe_male_average = data[index[1:]].groupby(index[1]).sum().sum(axis = 1) / data[index[1:]].groupby(index[1]).count().sum(axis =1)
fe_male_average.loc['Diff'] = fe_male_average.loc[0] - fe_male_average.loc[1]

print(fe_male_average)

'''
'''
df = data[index[1:]].groupby(index[1])
fe_male = df[index[2:]].get_group(0).apply(pd.value_counts).reset_index()
female = fe_male[fe_male['index'].isin(['4','5'])].sum() / fe_male[index[2:]].sum()
male = df[index[2:]].get_group(1).apply(pd.value_counts).reset_index()
male = male[male['index'].isin(['4','5'])].sum() / male[index[2:]].sum()
res = pd.concat([female,male],axis = 1)
res['Diff'] = res[0] - res[1]
print(res)
'''