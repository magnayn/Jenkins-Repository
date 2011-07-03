package com.nirima.jenkins;
import lib.LayoutTagLib

l=namespace(LayoutTagLib)
t=namespace("/lib/hudson")
st=namespace("jelly:stapler")
f=namespace("/lib/form")


f.dropdownDescriptorSelector(field:"upstream",title:"Upstream Type")

//f.entry(title:_("Upstream"), field:"upstream") {
//    select(name:"upstream") {
//        option(value:"upstream", _("Upstream Project"))
//        option(value:"specific", _("Specific Project"))
//    }
//  }

