cmake_minimum_required(VERSION 3.4.1)


add_library(secp256k1 SHARED IMPORTED)
set_target_properties(secp256k1 PROPERTIES IMPORTED_NO_SONAME 1 IMPORTED_LOCATION "${CMAKE_SOURCE_DIR}/libs/${ANDROID_ABI}/libSecp256k1.so")

set(srcDir "${CMAKE_SOURCE_DIR}/src/main/cpp/")
aux_source_directory(${srcDir} libSrcDir)
aux_source_directory(${srcDir}/libs libSrcDir)
aux_source_directory(${srcDir}/libs/libsecp256k1 libSrcDir)
aux_source_directory(${srcDir}/libs/libscrypt libSrcDir)
add_library(NebWalletCore SHARED ${libSrcDir})

# 导入cpp目录下的所有头文件
include_directories(src/main/cpp/)

find_library(log-lib log)

target_link_libraries(NebWalletCore secp256k1 ${log-lib})