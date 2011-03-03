import os
import sys

from pyroutes import application

path = os.path.dirname(__file__)
if path not in sys.path:
    sys.path.append(path)

import cloudstorage.fileserver

if __name__ == '__main__':
    from pyroutes import utils
    utils.devserver(application)
