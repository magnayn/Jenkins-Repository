package com.nirima.jenkins

import lib.LayoutTagLib

l=namespace(LayoutTagLib)
t=namespace("/lib/hudson")
st=namespace("jelly:stapler")
f=namespace("/lib/form")


f.entry(title:_("Project"), field:"project") {
     select(name:"project") {
        descriptor.getJobs().each() { item ->
            f.option(selected:item.name==instance?.project, value:item.name, _(item.name))
        }
     }
}

f.entry(title:_("Build"), field:"build") {
     select(name:"build") {
        f.option(selected:instance?.build=="repository", value:"repository", _("LastSuccessful / repository"))
        f.option(selected:instance?.build=="repositoryChain", value:"repositoryChain", _("LastSuccessful / repositoryChain"))
    }
}

