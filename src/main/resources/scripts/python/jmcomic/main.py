import sys
import jmcomic
import json
from PIL import Image, ImageFilter
import os

# Press the green button in the gutter to run the script.
# 命令：python main.py download_album
if __name__ == '__main__':
    option = jmcomic.create_option_by_file('./option.yml')
    res = jmcomic.download_photo(sys.argv[1], option)
    if res is None or res[0] is None:
        print("下载失败：", sys.argv[1])
    else:
        env_vars = os.environ
        jm_dir = env_vars['JM_DIR'] + os.sep + 'jm'
        image = Image.open(jm_dir + os.sep + res[0].title + os.sep + '00001.jpg')
        blurred_image = image.filter(ImageFilter.GaussianBlur(radius=10))
        blurred_image.save(jm_dir + os.sep + res[0].title + os.sep + 'view.jpg')
        print(json.dumps(res[0], default=lambda o: o.__dict__))

# See PyCharm help at https://www.jetbrains.com/help/pycharm/
