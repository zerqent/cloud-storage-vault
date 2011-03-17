# coding: utf-8
from wsgiref.util import FileWrapper

from pyroutes import route
from pyroutes.http.response import Response

from cloudstorage.filesystem import (retrieve_file, save_file,
                                     FileSystemException)

@route('/')
def index(request):
    return Response('Secure Cloud Storage Vault')

@route('/get')
def get_file(request):
    '''IANA manages a registry of media types,
    http://www.iana.org/assignments/media-types/

    '''
    storage_index = request.POST.get('storage_index', None)
    if storage_index is not None:
        try:
            file_to_send, size = retrieve_file(storage_index)
        except FileSystemException, e:
            return Response(e.text, status_code=e.code)

        headers = [('Content-Type', 'application/octet-stream'),
                   ('Content-Length', str(size))]
        return Response(file_to_send, headers)

    return Response('Did not receive any storage index.', status_code=400)

@route('/put')
def put_file(request, storage_index=None, write_enabler=None):
    if write_enabler is not None and write_enabler == "":
        # The doc of pyroutes states that "variables not available from the URL
        # is passed to your method as an empty string or your defined default in
        # the method declaration." But apparently not.
        write_enabler = None

    if storage_index is not None:
        if request.PUT is not None:
            try:
                content_length = request.ENV['CONTENT_LENGTH']
                save_status = save_file(storage_index, request.PUT,
                                        content_length, write_enabler)
                if save_status:
                    return Response('File received')
                else:
                    # File Access Problems?
                    return Response(status_code=500)
            except FileSystemException, e:
                return Response(e.text, status_code=e.code)

    return Response('No file given', status_code=400)

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
