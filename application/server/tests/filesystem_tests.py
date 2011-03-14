# coding: utf-8
import os
import random
import string
import unittest

from cloudstorage.filesystem import (FileSystemException, save_file,
                                     retrieve_file)
from cloudstorage.settings import FILE_STORE

class TestFileOperationPrimitives(unittest.TestCase):
    def __create_random_file(self, contents=None, path=FILE_STORE):
        si = ''.join(random.sample(string.letters+string.digits, 12))
        fp = open(os.path.join(path, si), 'w')
        if contents is not None:
            fp.write(contents)
        else:
            fp.write('TESTING')
        fp.close()
        return si

    def setUp(self):
        pass

    def test_get_of_file_should_return_file_pointer_yielding_correct_file(self):
        si = self.__create_random_file('GET_TEST')
        file_iterator, size = retrieve_file(si)
        result = file_iterator.next()

        self.assertEqual(result, 'GET_TEST')

    def test_save_new_file_should_return_true(self):
        si = ''.join(random.sample(string.letters+string.digits, 12))
        return_code = save_file(si, 'TESTING')

        fp = open(os.path.join(FILE_STORE, si), 'r')
        result = fp.read()
        fp.close()

        self.assertTrue(return_code)
        self.assertEqual(result, 'TESTING')

    def test_save_file_with_existing_si_but_no_we_should_fail(self):
        si = self.__create_random_file()

        self.assertRaises(FileSystemException, save_file, si, 'kjdfkj')

        fp = open(os.path.join(FILE_STORE, si), 'r')
        result = fp.read()
        fp.close()

        self.assertEqual(result, 'TESTING')

    def test_save_file_with_existing_si_and_correct_we_should_return_true(self):
        si = ''.join(random.sample(string.letters+string.digits, 12))
        we = ''.join(random.sample(string.letters+string.digits, 12))

        result = save_file(si, 'NEW_FILE_WITH_WE', we)
        updated = save_file(si, 'UPDATED_FILE', we)

        fp = open(os.path.join(FILE_STORE, si), 'r')
        content = fp.read()
        fp.close()

        self.assertTrue(result)
        self.assertTrue(updated)
        self.assertEqual(content, 'UPDATED_FILE')

    def test_save_file_with_existing_si_and_wrong_we_should_raise_exception(self):
        si = ''.join(random.sample(string.letters+string.digits, 12))
        we = ''.join(random.sample(string.letters+string.digits, 12))
        wrong_we = ''.join(random.sample(string.letters+string.digits, 12))

        result = save_file(si, 'NEW_FILE_WITH_WE', we)
        self.assertRaises(FileSystemException, save_file, si, 'ERROR', wrong_we)

        fp = open(os.path.join(FILE_STORE, si), 'r')
        content = fp.read()
        fp.close()

        self.assertTrue(result)
        self.assertEqual(content, 'NEW_FILE_WITH_WE')

    def test_save_new_file_with_correct_we_should_return_true(self):
        si = ''.join(random.sample(string.letters+string.digits, 12))
        we = ''.join(random.sample(string.letters+string.digits, 12))
        result = save_file(si, 'NEW_FILE_WITH_WE', we)

        fp = open(os.path.join(FILE_STORE, si), 'r')
        content = fp.read()
        fp.close()

        self.assertTrue(result)
        self.assertEqual('NEW_FILE_WITH_WE', content)

if __name__ == '__main__':
    unittest.main()
