from pyroutes import route
from pyroutes.http.response import Response

from cloudstorage.filesystem import (retrieve_file,
                                     FileSystemException)

@route('/get')
def get_file(request, storage_index=None):
    if storage_index is not None:
        try:
            file_to_send, size = retrieve_file(storage_index)
        except FileSystemException, e:
            return Response(e.text, status_code=e.code)

        headers = [('Content-Type', 'application/octet-stream'),
                   ('Content-Length', str(size))]
        return Response(file_to_send, headers)

    return Response('No resource ID given.', status_code=400)
