import os
import sys

path = os.path.dirname(__file__)
if path not in sys.path:
    sys.path.append(path)

from pyroutes import application

import cloudstorage.fileserver

if __name__ == '__main__':
    from pyroutes import utils
    utils.devserver(application)
