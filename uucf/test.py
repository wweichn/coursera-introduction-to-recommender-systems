import xlrd
import xlsxwriter


file = xlrd.open_workbook('./UUCF.xls')
print(file.sheet_names())


#name
'''
sheet1 = file.sheet_names()[0]
sheet2 = file.sheet_names()[1]
sheet3 = file.sheet_names()[2]
'''

#content
sheet1 = file.sheet_by_index(0)
sheet2 = file.sheet_by_index(1)
sheet3 = file.sheet_by_index(2)

corres = []
print(sheet3.row(7))

#for cell in sheet3.row(14):
for cell in sheet3.row(14):
    corres.append(cell.value)
movies = []
for cell in sheet1.col(0)[1:]:
    movies.append(cell.value)

print(corres)
max_index = sorted(range(len(corres)), key=lambda i: corres[i], reverse=True)
print(max_index)

dic = {}
max = 0
nominator = 0
denominator = 0
a = 0
for i in range(len(movies)):
   # print(i)
    max = 0
    nominator = 0
    denominator = 0
    for j in max_index[2:]:
        user_rating = sheet1.cell(i + 1, j).value
        if(user_rating != ""):
            nominator += corres[j] * user_rating
            denominator += corres[j]
        else:
            nominator += 0
            denominator += 0
        max += 1
        if(max == 5 and denominator != 0):

            dic[movies[i]] = nominator / denominator
            print(nominator/denominator)
            a += 1
          # print(nominator / denominator)
    if(max < 5):
        dic[movies[i]] = nominator / denominator
        print(nominator/denominator)


dic = dict(sorted(dic.items(), key = lambda d:d[1], reverse=True))


file_ = xlsxwriter.Workbook('data1.xlsx')
worksheet = file_.add_worksheet()
row = 0
col = 0
for key in dic.keys():
    worksheet.write(row,col, key)
    worksheet.write(row, col + 1, dic[key])
    row += 1





    #print(corres[i])

    #print(sheet3.row(0)[j].value)



