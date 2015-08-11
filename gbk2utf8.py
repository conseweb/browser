# -*- coding: utf-8 -*-
#! /usr/bin/env python

import os, os.path

# Code Page 936 (GBK) to UTF-8 Transcoder
# Author: Kristian Tang (@Krisiouz)
# A small script that converts a file encoded in Code Page 936 (GBK) to UTF-8.

def gbk_to_utf8(input_file, output_file):
    # Load Files
    if not os.path.isdir(input_file):
        input_file_opened = open(input_file, 'r')
        input_file_read = input_file_opened.read()
        output_file_opened = open(output_file, 'wb')
        # Transcode
        print('Transcoding…')
        try:
            us = unicode(input_file_read.decode('gbk'))
            output_file_opened.write(us.encode('utf-8'))
            input_file_opened.close()
            output_file_opened.close()
            print('Done.\n')
        except:
            pass


x = '/Users/michael/mojing/browser/app/src/main/java'
def func(arg, dirname, names):
    for filespath in names:
        inf = os.path.join(dirname, filespath)
        output_file = inf.replace(x, x+'_utf')
        # create output dir
        if not os.path.exists(os.path.dirname(output_file)):
            os.mkdir(os.path.dirname(output_file))
        print inf
        print output_file
        gbk_to_utf8(inf, output_file)
        

def main(p=x):
    print('Code Page 936 (GBK) to UTF-8 Transcoder\n')
    # Ask the User Which File to Transcode
    os.path.walk(p, func, None)
    

if __name__ == '__main__':
    main()