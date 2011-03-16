# coding: utf-8
import errno
import os
from wsgiref.util import FileWrapper

from cloudstorage.settings import FILE_STORE
from cloudstorage.db import get_db_connection

class FileSystemException(Exception):
    def __init__(self, text, code=500):
        self.text = text
        self.code = code
    def __str__(self):
        return '%s %s' % (repr(self.text), repr(self.code))

def check_write_enabler(storage_index, write_enabler):
    db_connection = get_db_connection()
    cursor = db_connection.cursor()
    cursor.execute('SELECT * FROM write_enablers WHERE storage_index = %s',
                  (storage_index,))
    result = cursor.fetchone()
    db_connection.close()

    if result is not None:
        return result[1] == write_enabler
    else:
        raise FileSystemException(u'File does not have storage index registered'
                                  u', is it an immutable file?', 403)

    return False

def get_path(storage_index):
    path = os.path.join(FILE_STORE, storage_index)
    if os.path.exists(path):
        return path
    else:
        return None

def save_file(storage_index, data, data_length, write_enabler=None):
    if storage_index is not None:
        try:
            fd = os.open(os.path.join(FILE_STORE, storage_index),
                                      os.O_WRONLY | os.O_CREAT | os.O_EXCL)
            fp = os.fdopen(fd, 'w')

            if write_enabler is not None:
                db_connection = get_db_connection()
                cursor = db_connection.cursor()
                cursor.execute('INSERT INTO write_enablers VALUES (%s, %s)',
                              (storage_index, write_enabler))
                db_connection.commit()
                db_connection.close()
        except OSError, e:
            if e.errno == errno.EEXIST:
                # The file already exist
                if check_write_enabler(storage_index, write_enabler):
                    fp = open(os.path.join(FILE_STORE, storage_index), 'w')
                else:
                    raise FileSystemException('Wrong write-enabler', 401)
            elif e.errno == errno.EACCES:
                # We have problems getting access to FILE_STORE
                return False

        #FIXME: Probably want some sort of loop to read chunks at the time.
        fp.write(data.read(int(data_length)))
        fp.close()

        return True

    raise FileSystemException('Missing storage index')

def retrieve_file(storage_index):
    if storage_index is not None:
        path = get_path(storage_index)
        if path is not None:
            file_iterator = FileWrapper(open(path, 'rb'))
            size = os.path.getsize(path)
            return (file_iterator, size)

    raise FileSystemException('Could not find file.', 400)
