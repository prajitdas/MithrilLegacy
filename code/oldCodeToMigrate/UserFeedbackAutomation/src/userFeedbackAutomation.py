#! /usr/bin/env python
# -*- coding: utf-8 -*-
'''
Created on Apr 14, 2015
@author: Prajit Kumar Das
Usage: python userFeedbackAutomation.py
'''
#
# import sys
# # Imports the monkeyrunner modules used by this program
# from com.android.monkeyrunner import MonkeyRunner, MonkeyDevice
# from com.android.monkeyrunner.easy import EasyMonkeyDevice, By
# '''
# Copyright (C) 2012  Diego Torres Milano
# Created on Feb 3, 2012
#
# @author: diego
# '''

import sys
import os
import re
import time

# This must be imported before MonkeyRunner and MonkeyDevice,
# otherwise the import fails.
# PyDev sets PYTHONPATH, use it
try:
    for p in os.environ['PYTHONPATH'].split(':'):
        if not p in sys.path:
            sys.path.append(p)
except:
    pass

try:
    sys.path.append(os.path.join(os.environ['ANDROID_VIEW_CLIENT_HOME'], 'src'))
except:
    pass

from com.dtmilano.android.viewclient import *

'''
Sample
def main(argv):
    # Connects to the current device, returning a MonkeyDevice object
    device = MonkeyRunner.waitForConnection()

    # Installs the Android package. Notice that this method returns a boolean, so you can test
    # to see if the installation worked.
    device.installPackage('myproject/bin/MyApplication.apk')

    # sets a variable with the package's internal name
    package = 'com.example.android.myapplication'

    # sets a variable with the name of an Activity in the package
    activity = 'com.example.android.myapplication.MainActivity'

    # sets the name of the component to start
    runComponent = package + '/' + activity

    # Runs the component
    device.startActivity(component=runComponent)

    # Presses the Menu button
    device.press('KEYCODE_MENU', MonkeyDevice.DOWN_AND_UP)

    # Takes a screenshot
    result = device.takeSnapshot()

    # Writes the screenshot to a file
    result.writeToFile('myproject/shot1.png','png')
'''

def main(argv):
    package = 'edu.umbc.cs.ebiquity.mithril'
    activity = 'edu.umbc.cs.ebiquity.mithril.ui.ViolationWarningActivity'
    component = package + "/" + activity

    device, serialno = ViewClient.connectToDeviceOrExit()
    device.startActivity(component=component)
    time.sleep(3)
    vc = ViewClient(device, serialno)
    if vc.build[VERSION_SDK_PROPERTY] != 17:
        print 'This script is intended to run on API-17'
        sys.exit(1)
    ALL_PICTURES = 'All pictures'
    vc.findViewWithTextOrRaise(re.compile('%s \(\d+\)' % ALL_PICTURES)).touch()
    vc.dump()
    vc.findViewWithTextOrRaise(ALL_PICTURES)
    print "'%s' found" % ALL_PICTURES
'''
    # Connects to the current device, returning a MonkeyDevice object
    device = MonkeyRunner.waitForConnection()

	easy_device = EasyMonkeyDevice(device)

    # Installs the Android package. Notice that this method returns a boolean, so you can test
    # to see if the installation worked.
    device.installPackage('../../MithrilAPKs/UserFeedbackModule.apk')

    # sets a variable with the package's internal name
	package = "edu.umbc.cs.ebiquity.mithril"

    # sets a variable with the name of an Activity in the package
	activity = "edu.umbc.cs.ebiquity.mithril.ui.ViolationWarningActivity"

    # sets the name of the component to start
    runComponent = package + '/' + activity

    # Runs the component
    device.startActivity(component=runComponent)

	# Presses the Menu button
    device.press('KEYCODE_MENU', MonkeyDevice.DOWN_AND_UP)

	# touch the view by id
	easy_device.touch(By.id('view_id'), MonkeyDevice.DOWN_AND_UP)

    # Takes a screenshot
    result = device.takeSnapshot()

    # Writes the screenshot to a file
    result.writeToFile('../out/shot1.png','png')
'''
if __name__ == "__main__":
    sys.exit(main(sys.argv))
