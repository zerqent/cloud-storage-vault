try:
    from httplib import reponses
except ImportError:
    responses = {
        '401': 'Unauthorized',
    }

def get_http_code(code):
    if code in responses.keys():
        return '%s %s' % (code, responses[code])
