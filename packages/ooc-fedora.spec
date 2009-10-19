Name:           ooc
Version:        0.3
Release:        1%{?dist}
Summary:        OOC is a oriented object programming language C99 compliant

Group:          Development/Tools
License:        BSD
URL:            http://www.ooc-lang.orf
Source0:        ooc-%{version}.tar.bz2 
BuildRoot:      %{_tmppath}/%{name}-%{version}-%{release}-root-%(%{__id_u} -n)

BuildRequires:  gcc-java ant
Requires:       gcc make java-1.5.0-gcj gc-devel

%description
ooc is a modern, object-oriented, functional-ish, high-level, low-level,
sexy programming language. it's translated to pure C with a 
source-to-source compiler. it strives to be powerful, modular,
extensible, portable, yet simple and fast. 

%prep
%setup -q


%build
make %{?_smp_mflags} gcj-dynamic

%install
rm -rf $RPM_BUILD_ROOT
make install-bin DESTDIR=$RPM_BUILD_ROOT/usr ARCH=%{_arch}
sed -i "s,$RPM_BUILD_ROOT,," $RPM_BUILD_ROOT/%{_bindir}/ooc

#remove static compilation libs
rm -rf $RPM_BUILD_ROOT/%{_datadir}/ooc/libs

%clean
rm -rf $RPM_BUILD_ROOT


%files
%defattr(-,root,root,-)
%{_bindir}/ooc
%{_datadir}/ooc/sdk
%{_libdir}/ooc

%doc README



%changelog
* Mon Oct 19 2009 Patrice Ferlet <metal3d@gmail.com> 0.3-1.fc11
- Initial package 
