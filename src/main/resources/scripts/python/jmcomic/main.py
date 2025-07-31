#code: UTF-8
import sys
import jmcomic
import json
import os

# Press the green button in the gutter to run the script.
# 命令：python main.py download_album
if __name__ == '__main__':
    option = jmcomic.create_option_by_file(os.environ['JM_CONFIG'])
    res = jmcomic.download_photo(sys.argv[1], option)
    if res is None or res[0] is None:
        print("下载失败：", sys.argv[1])
    else:
        print(json.dumps(res[0], default=lambda o: o.__dict__))

# See PyCharm help at https://www.jetbrains.com/help/pycharm/
