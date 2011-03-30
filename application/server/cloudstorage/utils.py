# encoding: utf-8

def _clean_input(parameter):
    '''Sets parameter to None if it is an empty string.

    The doc of pyroutes states that "variables not available from the URL
    is passed to your method as an empty string or your defined default in
    the method declaration." But apparently not.

    '''
    if parameter is not None and parameter == "":
        return None
    return parameter

