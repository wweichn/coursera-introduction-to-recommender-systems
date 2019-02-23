import xlrd
import xlsxwriter

file = xlrd.open_workbook('./Assignment5.xls')

sheet1 = file.sheet_by_index(0)
sheet2 = file.sheet_by_index(1)
sheet3 = file.sheet_by_index(2)
sheet4 = file.sheet_by_index(3)


dic = {}
index  = 0

for movie in sheet3.col(0)[1:]:
    j = 1
    index += 1
    nominator = 0
    denominator = 0
    print("ok")
    for cell_ in sheet3.row(index)[1:]:
        rating = sheet1.row(2)[j].value
        if(rating == ""):
            j += 1
            continue
        else:
            print("j",j)
            corres = cell_.value
            print("rating",rating,"corres",corres)
            nominator += corres * rating
            print("nomi",nominator)
            denominator += corres
            print(corres)
            j += 1
    dic[movie.value] = nominator / denominator

dic = dict(sorted(dic.items(), key = lambda d:d[1],reverse = True))

file_ = xlsxwriter.Workbook('data1.xlsx')
worksheet = file_.add_worksheet()
row = 0
col = 0
for key in dic.keys():
    worksheet.write(row,col,key)
    worksheet.write(row,col+1,dic[key])
    row += 1


