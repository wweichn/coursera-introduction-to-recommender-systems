import xlrd
import xlsxwriter

file = xlrd.open_workbook('./Assignment5.xls')

sheet1 = file.sheet_by_index(0)
sheet2 = file.sheet_by_index(1)
sheet3 = file.sheet_by_index(2)
sheet4 = file.sheet_by_index(3)
sheet5 = file.sheet_by_index(4)

dic = {}
index  = 0

for movie in sheet4.col(0)[1:]:
    j = 1
    index += 1
    nominator = 0
    denominator = 0
    for cell_ in sheet4.row(index)[1:]:
        rating = sheet2.row(2)[j].value
        if(cell_.value < 0 or rating == 0):
            j += 1
            continue
        else:
            corres = cell_.value
            print("rating",rating)
            nominator += corres * rating
            denominator += corres
            print(corres)
            j += 1
    print(index)
    dic[movie.value] = nominator / denominator + sheet1.row(2)[21].value

dic = dict(sorted(dic.items(), key = lambda d:d[1],reverse = True))

file_ = xlsxwriter.Workbook('data3.xlsx')
worksheet = file_.add_worksheet()
row = 0
col = 0
for key in dic.keys():
    worksheet.write(row,col,key)
    worksheet.write(row,col+1,dic[key])
    row += 1


