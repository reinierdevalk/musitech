#!/usr/bin/env python
import os, os.path

def addLicenseTextRecursive(path,license):
	for file in os.listdir(path):
		fullname = os.path.join(path,file)
		if os.path.isdir(fullname):
			addLicenseTextRecursive(fullname,license)
		if os.path.isfile(fullname) and fullname.endswith('.java'):
			f = open(fullname,'r+')
			content = f.read()
			if content.find(license)==-1:
				content = license + content
				f.seek(0)
				f.truncate()
				f.write(content)
				print("License written to "+fullname)
			f.close()
			
f = open(os.path.join(os.getcwd(),'license.txt'))
license = f.read()
f.close()
addLicenseTextRecursive(os.getcwd(),license)