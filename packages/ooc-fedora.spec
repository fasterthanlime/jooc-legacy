Name:           ooc
Version:        0.3.git20091101
Release:        1%{?dist}
Summary:        Oriented Object C programming language C99 compliant

Group:          Development/Tools
License:        BSD
URL:            http://www.ooc-lang.org
Source0:        ooc-%{version}.tar.bz2 
Source1:        ooc-gtk-%{version}.tar.bz2 
Source2:        ooc-gdk-%{version}.tar.bz2 
BuildRoot:      %{_tmppath}/%{name}-%{version}-%{release}-root-%(%{__id_u} -n)

BuildRequires:  gcc-java ant
Requires:       gcc make java-1.5.0-gcj gc-devel

%description
ooc is a modern, object-oriented, functional-ish, high-level, low-level,
sexy programming language. it's translated to pure C with a 
source-to-source compiler. it strives to be powerful, modular,
extensible, portable, yet simple and fast. 

%package gtk
Requires:       gtk2-devel ooc ooc-gdk
Summary:        Gtk bindings for ooc

%description gtk
ooc is a modern, object-oriented, functional-ish, high-level, low-level,
sexy programming language. it's translated to pure C with a 
source-to-source compiler. it strives to be powerful, modular,
extensible, portable, yet simple and fast. 

%package gdk
Requires:       gdk-devel ooc
Summary:        Gdk bindings for ooc

%description gdk
ooc is a modern, object-oriented, functional-ish, high-level, low-level,
sexy programming language. it's translated to pure C with a 
source-to-source compiler. it strives to be powerful, modular,
extensible, portable, yet simple and fast. 

%prep
%setup -q  -a 1 -a 2


%build
make %{?_smp_mflags} gcj-dynamic

%install
rm -rf $RPM_BUILD_ROOT
make install-bin DESTDIR=$RPM_BUILD_ROOT/usr ARCH=%{_arch}
sed -i "s,$RPM_BUILD_ROOT,," $RPM_BUILD_ROOT/%{_bindir}/ooc

#remove static compilation libs
rm -rf $RPM_BUILD_ROOT/%{_datadir}/ooc/libs

#install gtk
mkdir -p  $RPM_BUILD_ROOT/%{_libdir}/ooc/gtk
cp -r ooc-gtk-%{version}/gtk ooc-gtk-%{version}/babbisch ooc-gtk-%{version}/gtk.use $RPM_BUILD_ROOT/%{_libdir}/ooc/gtk

#install gdk
mkdir -p  $RPM_BUILD_ROOT/%{_libdir}/ooc/gdk
cp -r ooc-gdk-%{version}/gdk ooc-gdk-%{version}/gdk.use $RPM_BUILD_ROOT/%{_libdir}/ooc/gdk


%clean
rm -rf $RPM_BUILD_ROOT


%files
%defattr(-,root,root,-)
%{_bindir}/ooc
%{_datadir}/ooc/sdk
%{_libdir}/ooc

%doc README

%files gtk
%{_libdir}/ooc/gtk

%files gdk
%{_libdir}/ooc/gdk

%changelog
* Mon Oct 19 2009 Patrice Ferlet <metal3d@gmail.com> 0.3-1.fc11
- Initial package 
