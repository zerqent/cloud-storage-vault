# coding: utf-8
import unittest
import random
import string
import os

from filesystem import FileSystemException, file_exists, save_file, FILE_STORE

class TestFileOperationPrimitives(unittest.TestCase):
    def setUp(self):
        self.si = ''.join(random.sample(string.letters+string.digits, 12))
        fp = open(os.path.join(FILE_STORE, self.si), 'w')
        fp.write('TESTING')

        fp.close()

    def test_save_file_with_existing_si_but_no_we_should_fail(self):
        self.assertRaises(FileSystemException, save_file, self.si, 'kjdfkj')

        fp = open(os.path.join(FILE_STORE, self.si), 'r')
        result = fp.read()
        fp.close()

        self.assertEqual(result, 'TESTING')

if __name__ == '__main__':
    unittest.main()
