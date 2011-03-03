# coding: utf-8

import psycopg2

from cloudstorage.settings import DB_CONF

def get_db_connection():
    connection = psycopg2.connect("""
        host=%(host)s port=%(port)s
        dbname=%(database)s user=%(user)s
        password=%(password)s""" % DB_CONF)
    return connection
