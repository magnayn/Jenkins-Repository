package com.nirima.jenkins

import lib.LayoutTagLib

l=namespace(LayoutTagLib)
t=namespace("/lib/hudson")
st=namespace("jelly:stapler")
f=namespace("/lib/form")

f.entry(title:_("Build"), field:"build") {
     select(name:"build") {
        option(value:"repository", _("LastSuccessful / repository"))
        option(value:"repositoryChain", _("LastSuccessful / repositoryChain"))
    }
}



