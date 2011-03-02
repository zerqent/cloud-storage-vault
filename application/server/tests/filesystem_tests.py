# coding: utf-8
import unittest
import random
import string
import os

from filesystem import (FileSystemException, save_file, FILE_STORE,
                        retrieve_file)

class TestFileOperationPrimitives(unittest.TestCase):
    def __create_random_file(self, contents=None):
        si = ''.join(random.sample(string.letters+string.digits, 12))
        fp = open(os.path.join(FILE_STORE, si), 'w')
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

    def test_save_new_file(self):
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

if __name__ == '__main__':
    unittest.main()
