# coding: utf-8
import unittest
import random
import string
import os

from filesystem import FileSystemException, save_file, FILE_STORE

class TestFileOperationPrimitives(unittest.TestCase):
    def setUp(self):
        pass

    def test_save_new_file(self):
        si = ''.join(random.sample(string.letters+string.digits, 12))
        return_code = save_file(si, 'TESTING')

        fp = open(os.path.join(FILE_STORE, si), 'r')
        result = fp.read()
        fp.close()

        self.assertTrue(return_code)
        self.assertEqual(result, 'TESTING')

    def test_save_file_with_existing_si_but_no_we_should_fail(self):
        print 'her'
        si = ''.join(random.sample(string.letters+string.digits, 12))
        fp = open(os.path.join(FILE_STORE, si), 'w')
        fp.write('TESTING')
        fp.close()

        self.assertRaises(FileSystemException, save_file, si, 'kjdfkj')

        fp = open(os.path.join(FILE_STORE, si), 'r')
        result = fp.read()
        fp.close()

        self.assertEqual(result, 'TESTING')

if __name__ == '__main__':
    unittest.main()
