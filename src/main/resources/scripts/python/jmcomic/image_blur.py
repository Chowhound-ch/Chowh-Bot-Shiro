import os
import sys
from pathlib import Path
from PIL import ImageFilter, Image
from curl_cffi import requests


def download_file(url, local_path):
    """下载网络文件到本地"""
    response = requests.get(url, stream=True)
    with open(local_path, 'wb') as f:
        for chunk in response.iter_content(chunk_size=8192):
            if chunk:
                f.write(chunk)
    print(f"文件已下载到: {local_path}")


def blur_and_save_image(input_path, output_path):
    image = Image.open(input_path)
    blurred_image = image.filter(ImageFilter.GaussianBlur(radius=10))
    blurred_image.save(output_path)


if __name__ == '__main__':
    org_file = sys.argv[1]
    to_file = sys.argv[2]
    if org_file.startswith("http"):
        download_file(org_file, to_file)
        org_file = to_file
        to_file = sys.argv[3]

    if os.path.isfile(org_file):  # 文件
        blur_and_save_image(org_file, to_file)
        print(to_file)
    elif os.path.isdir(org_file):
        path = Path(org_file)
        files = [file.name for file in path.rglob("*.*")]
        for file in files:
            res_file = to_file + os.path.basename(file)
            blur_and_save_image(file, res_file)
            print(res_file)
    else:
        raise FileNotFoundError(sys.argv[1:])
