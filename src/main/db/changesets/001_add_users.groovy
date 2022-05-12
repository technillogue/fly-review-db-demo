databaseChangeLog {
    changeSet(id: '1', author: 'demo') {
        createTable(tableName: 'users') {
            column(name: 'id', type: 'serial') {
                constraints(primaryKey: true)
            }
            column(name: 'name', type: 'text') {
                constraints(nullable: true)
            }
            column(name: 'about', type: 'text') {
                constraints(nullable: true)
            }
        }
    }
}
