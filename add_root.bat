cd ./data
del temporary_file

for %%f in (*) do (
    echo ^<ROOT^>>> temporary_file
    type %%f>> temporary_file
    echo.>> temporary_file
    echo ^</ROOT^>>> temporary_file

    del %%f
    ren temporary_file %%f
    del temporary_file
)
cd ..