package com.nirima.jenkins

import lib.LayoutTagLib

l=namespace(LayoutTagLib)
t=namespace("/lib/hudson")
st=namespace("jelly:stapler")
f=namespace("/lib/form")

f.entry(title:_("Build"), field:"build") {
     select(name:"build") {
        f.option(selected:instance?.build=="repository", value:"repository", _("LastSuccessful / repository"))
        f.option(selected:instance?.build=="repositoryChain", value:"repositoryChain", _("LastSuccessful / repositoryChain"))
    }
}



