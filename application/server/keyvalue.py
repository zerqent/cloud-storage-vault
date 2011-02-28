# coding: utf-8
from pyroutes import application
from pyroutes import route
from pyroutes.http.response import Response

from filesystem import check_write_enabler

@route('/')
def index(request):
    return Response('Hello cock')

@route('/get')
def get_file(request):
    storage_index = request.GET.get('si', None)
    if storage_index is not None:
        #open file to buffer
        buffer = ''
        return Response(buffer, [('Content-Type', 'application/encryptedfile')],
                        default_content_header=False)
    return Response('GET:')

@route('/put')
def put_file(request):
    if 'uploaded_file' in request.FILES:
        storage_index = request.FILES['uploaded_file'][0]
        if storage_index is not None:
            write_enabler = request.POST.get('we', None)
            if write_enabler is not None:
                if check_write_enabler(storage_index, write_enabler):
                    data = request.FILES['uploaded_file'][1].read()
                    response = write_file(storage_index, data)
                    return Response('PUT OK: %s' % storage_index)
                else:
                    return Response('Wrong write enabler',
                                    status_code='403 Forbidden')
    else:
        return Response('No file given')

if __name__ == '__main__':
    from pyroutes import utils
    route('/files')(utils.fileserver)
    utils.devserver(application)
