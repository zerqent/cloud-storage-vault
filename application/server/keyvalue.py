# coding: utf-8
import os
import sys
path = os.path.dirname(__file__)
if path not in sys.path:
    sys.path.append(path)

from pyroutes import application, route
from pyroutes.http.response import Response
import pyroutes.settings

from filesystem import save_file, FileSystemException

pyroutes.settings.DEBUG = True

@route('/')
def index(request):
    return Response('Hello cock')

@route('/get')
def get_file(request):
    '''IANA manages a registry of media types,
    http://www.iana.org/assignments/media-types/

    '''

    storage_index = request.GET.get('si', None)
    if storage_index is not None:
        #open file to buffer
        buffer = ''
        return Response(buffer, [('Content-Type', 'application/x-encrypted')],
                        default_content_header=False)
    return Response('GET:')

@route('/put')
def put_file(request):
    if 'encrypted_file' in request.FILES:
        storage_index = request.FILES['encrypted_file'][0]
        if storage_index is not None:
            write_enabler = request.POST.get('write_enabler', None)
            fileobj = request.FILES['encrypted_file'][1].read()
            try:
                save_status = put_file(storage_index, fileobj, write_enabler)
            except FileSystemException, e:
                return Response(e.text, status_code=e.code)

    return Response('No file/filename given')

@route('/test')
def test_ops(request):
    response = 'POST:<br>'
    response += ''.join(['%s: %s, ' % (str(key), str(value)) for key, value in request.POST.items()])
    response += '<br>GET:<br>'
    response += ''.join(['%s: %s, ' % (key, value) for key, value in request.GET.items()])
    response += '<br>FILES:<br>'
    if 'encrypted_file' in request.FILES:
        response += 'Name: %s' % request.FILES['encrypted_file'][0]
        fileobj = request.FILES['encrypted_file'][1].read()
        response += '<br><br> %s' % fileobj
    else:
        response += 'No file with id encrypted_file given'
    return Response(response)

if __name__ == '__main__':
    from pyroutes import utils
    route('/files')(utils.fileserver)
    utils.devserver(application)
