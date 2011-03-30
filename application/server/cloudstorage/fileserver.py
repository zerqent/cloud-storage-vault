# coding: utf-8
from pyroutes import route
from pyroutes.http.response import Response

from cloudstorage.filesystem import (retrieve_file, save_file,
                                     FileSystemException)
from cloudstorage.utils import _clean_input

@route('/')
def index(request):
    return Response('Secure Cloud Storage Vault')

@route('/get')
def get_file(request, storage_index=None):
    '''Handles GET requests for encrypted files.

    IANA manages a registry of media types,
    http://www.iana.org/assignments/media-types/

    '''
    if _clean_input(storage_index) is not None:
        try:
            file_to_send, size = retrieve_file(storage_index)
        except FileSystemException, e:
            return Response(e.text, status_code=e.code)

        headers = [('Content-Type', 'application/octet-stream'),
                   ('Content-Length', str(size))]
        return Response(file_to_send, headers)

    return Response('No resource ID given.', status_code=400)

@route('/put')
def put_file(request, storage_index=None, write_enabler=None):
    '''Handles HTTP PUT requests for new or existing files.

    If write_enabler is provided, it is further processed as an mutable
    file/directory.

    '''

    if _clean_input(storage_index) is not None:
        if request.PUT is not None:
            try:
                content_length = request.ENV['CONTENT_LENGTH']
                save_status = save_file(
                    storage_index,
                    request.PUT,
                    content_length,
                    _clean_input(write_enabler)
                    )

                return Response(status_code=save_status)
            except FileSystemException, e:
                return Response(e.text, status_code=e.code)

    return Response('No resource ID given.', status_code=400)

@route('/test')
def test_ops(request):
    '''Utility test function.'''
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
