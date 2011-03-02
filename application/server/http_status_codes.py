HTTP_CODES = {
    '401': 'Unauthorized',
}

def get_http_code(code):
    if code in HTTP_CODES.keys():
        return '%s %s' % (code, HTTP_CODES[code])
