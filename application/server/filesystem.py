# coding: utf-8
import os

FILE_STORE='/tmp/cloud'

class FileSystemException(Exception):
    def __init__(self, value):
        self.text = value
    def __str__(self):
        return repr(self.text)

def check_write_enabler(storage_index, write_enabler):
    return False

def file_exists(storage_index):
    pass

def save_file(storage_index, data, write_enabler=None):
    if storage_index is not None:
        try:
            fd = os.open(os.path.join(FILE_STORE, storage_index), 
                                      os.O_WRONLY | os.O_CREAT | os.O_EXCL)
            fp = os.fdopen(fd, 'w')
        except OSError:
            # The file already exist
            if check_write_enabler(storage_index, write_enabler):
                fp = open(os.path.join(FILE_STORE, storage_index), 'w')
            else:
                raise FileSystemException('Wrong write-enabler')

        fp.write(data)
        fp.close()

        return True
    
    raise FileSystemException('Missing storage index')
