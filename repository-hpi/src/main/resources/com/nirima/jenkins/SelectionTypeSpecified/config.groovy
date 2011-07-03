package com.nirima.jenkins

import lib.LayoutTagLib

l=namespace(LayoutTagLib)
t=namespace("/lib/hudson")
st=namespace("jelly:stapler")
f=namespace("/lib/form")


f.entry(title:_("Path"), field:"path") {
     f.textbox()
}

