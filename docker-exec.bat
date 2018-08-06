if [%1]==[] goto usage

docker exec -it %1 %2

goto :eof

:usage
@echo Usage: docker-exec.bat ^<container name^> ^<command^>
pause
exit /B 1

:eof

